package org.adorsys.ledgers.consent.aspsp.rest.client;

import de.adorsys.psd2.consent.api.CmsPageInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseData<D> {

    private D data;
    private CmsPageInfo pageInfo;
    private Object status;

    public ResponseData(D data, Object status) {
        this.data = data;
        this.status = status;
    }

    public ResponseData(D data, CmsPageInfo pageInfo, Object status) {
        this.data = data;
        this.pageInfo = pageInfo;
        this.status = status;
    }

    public static <D> ResponseData<D> entity(D data, Object status) {
        return new ResponseData<>(data, status);
    }

    public static <D> ResponseData<D> list(D data, CmsPageInfo info, Object status) {
        return new ResponseData<>(data, info, status);
    }
}
