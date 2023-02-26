package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class WebClient {

    private final RestTemplate restTemplate;
    private final String statUrl;

    private final String appName;

    public WebClient(RestTemplate restTemplate, @Value("${stats-server.url}") String statUrl, @Value("${APP}") String appName) {
        this.restTemplate = restTemplate;
        this.statUrl = statUrl;
        this.appName = appName;
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
