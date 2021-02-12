package org.adorsys.ledgers.consent.mixin;

import de.adorsys.psd2.consent.api.CmsPageInfo;
import de.adorsys.psd2.consent.api.ResponseData;

@SuppressWarnings({"PMD.UnusedPrivateField", "java:S1068"})
public class ResponseDataMixIn<D> extends ResponseData<D> {
    public ResponseDataMixIn() {
        super(null, null);
    }

    public ResponseDataMixIn(D data, Object status) {
        super(data, status);
    }

    public ResponseDataMixIn(D data, CmsPageInfo pageInfo, Object status) {
        super(data, pageInfo, status);
    }
}
