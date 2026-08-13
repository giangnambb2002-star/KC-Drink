package com.example.datn.van_chuyen.client;

import com.example.datn.van_chuyen.dto.GhnCreateOrderRequest;
import com.example.datn.van_chuyen.dto.GhnDistrictRequest;
import com.example.datn.van_chuyen.dto.GhnFeeRequest;
import com.example.datn.van_chuyen.dto.GhnLeadTimeRequest;
import com.example.datn.van_chuyen.dto.GhnOrderDetailRequest;
import com.example.datn.van_chuyen.dto.GhnServiceRequest;
import com.example.datn.van_chuyen.dto.GhnWardRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GhnClient {

    private final RestTemplate restTemplate;

    @Value("${ghn.base-url}")
    private String baseUrl;

    @Value("${ghn.token}")
    private String token;

    @Value("${ghn.shop-id}")
    private Integer shopId;

    public JsonNode getProvinces() {
        String url = baseUrl + "/master-data/province";
        HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders(false));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode getDistricts(Integer provinceId) {
        String url = baseUrl + "/master-data/district";
        GhnDistrictRequest requestBody = new GhnDistrictRequest(provinceId);
        HttpEntity<GhnDistrictRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(false));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode getWards(Integer districtId) {
        String url = baseUrl + "/master-data/ward";
        GhnWardRequest requestBody = new GhnWardRequest(districtId);
        HttpEntity<GhnWardRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(false));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode calculateFee(GhnFeeRequest requestBody) {
        String url = baseUrl + "/v2/shipping-order/fee";
        HttpEntity<GhnFeeRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(true));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode calculateLeadTime(GhnLeadTimeRequest requestBody) {
        String url = baseUrl + "/v2/shipping-order/leadtime";
        HttpEntity<GhnLeadTimeRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(true));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode getAvailableServices(Integer fromDistrictId, Integer toDistrictId) {
        String url = baseUrl + "/v2/shipping-order/available-services";

        GhnServiceRequest requestBody = new GhnServiceRequest(
                shopId,
                fromDistrictId,
                toDistrictId
        );

        HttpEntity<GhnServiceRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(false));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode previewOrder(GhnCreateOrderRequest requestBody) {
        String url = baseUrl + "/v2/shipping-order/preview";
        HttpEntity<GhnCreateOrderRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(true));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode createOrder(GhnCreateOrderRequest requestBody) {
        String url = baseUrl + "/v2/shipping-order/create";
        HttpEntity<GhnCreateOrderRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(true));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    public JsonNode getOrderDetail(String orderCode) {
        String url = baseUrl + "/v2/shipping-order/detail";
        GhnOrderDetailRequest requestBody = new GhnOrderDetailRequest(orderCode);
        HttpEntity<GhnOrderDetailRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders(false));

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            ).getBody();
        } catch (HttpStatusCodeException exception) {
            throw createGhnException(exception);
        } catch (RestClientException exception) {
            throw createConnectionException(exception);
        }
    }

    private HttpHeaders createHeaders(boolean includeShopId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Token", token);

        if (includeShopId) {
            headers.set("ShopId", String.valueOf(shopId));
        }

        return headers;
    }

    private RuntimeException createGhnException(HttpStatusCodeException exception) {
        String responseBody = exception.getResponseBodyAsString();

        if (responseBody == null || responseBody.isBlank()) {
            responseBody = exception.getMessage();
        }

        return new RuntimeException(
                "GHN trả về lỗi "
                        + exception.getStatusCode().value()
                        + ": "
                        + responseBody
        );
    }

    private RuntimeException createConnectionException(RestClientException exception) {
        return new RuntimeException(
                "Không thể kết nối đến hệ thống GHN: " + exception.getMessage(),
                exception
        );
    }
}