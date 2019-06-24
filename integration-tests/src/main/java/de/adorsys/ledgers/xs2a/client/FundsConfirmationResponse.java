/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.ledgers.xs2a.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "FundsConfirmationResponse", value = "Funds confirmation response")
public class FundsConfirmationResponse {
    @ApiModelProperty(value = "Equals 'true' if sufficient funds are available at the time of the request, 'false' otherwise.", example = "true")
    private boolean fundsAvailable;
    private Object errorHolder;

    public boolean isFundsAvailable() {
        return fundsAvailable;
    }

    public void setFundsAvailable(boolean fundsAvailable) {
        this.fundsAvailable = fundsAvailable;
    }

    public Object getErrorHolder() {
        return errorHolder;
    }

    public void setErrorHolder(Object errorHolder) {
        this.errorHolder = errorHolder;
    }
}
