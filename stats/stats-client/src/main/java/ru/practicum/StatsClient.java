package ru.practicum;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StatsClient extends BaseClient {

    private final String application;
    private final String statsServiceUri;
    private final ObjectMapper json;
    private final HttpClient httpClient;

    private final RestTemplate restTemplate;
    private final String statUrl;

    private final String appName;

    @Autowired
    public StatsClient(@Value("${spring.application.name}") String application,
                       @Value("${stats-server.url}") String statsServiceUri,
                       ObjectMapper json,
                       RestTemplateBuilder builder, RestTemplate restTemplate, String statUrl, String appName) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(statsServiceUri))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.application = application;
        this.statsServiceUri = statsServiceUri;
        this.json = json;
        this.restTemplate = restTemplate;
        this.statUrl = statUrl;
        this.appName = appName;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public void hit(HttpServletRequest userRequest) {
        HitDto hit = HitDto.builder()
                .app(application)
                .ip(userRequest.getRemoteAddr())
                .uri(userRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        post("/hit", hit);
    }

    public void addToStatistic(HttpServletRequest httpServletRequest) {
        HitDto hitDto = new HitDto();
        hitDto.setApp(appName);
        hitDto.setIp(httpServletRequest.getRemoteAddr());
        hitDto.setUri(httpServletRequest.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.now());
        String url = statUrl + "/hit";
        HttpEntity<HitDto> request = new HttpEntity<>(hitDto);
        restTemplate.postForObject(url, request, HitDto.class);
    }

    public Integer findView(Long eventId) {
        ViewStatsDto[] views = restTemplate.getForObject(statUrl + "/stats?uris=/events/"
                + eventId.toString(), ViewStatsDto[].class);
        if (views != null) {
            if (views.length > 0) {
                return views[0].getHits().intValue();
            }
        }
        return null;

    }

    public ViewStatsDto[] getViews(List<Long> dtos) {
        StringBuilder sb = new StringBuilder(statUrl + "/stats?uris=");
        for (int i = 0; i < dtos.size(); i++) {
            if (i > 0 && i < dtos.size() - 1) {
                sb.append(",");
            }
            sb.append("/events/").append(dtos.get(i));
        }
        return restTemplate.getForObject(sb.toString(), ViewStatsDto[].class);
    }
    }
