/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

function sendPaymentRequestAndGetResponse(productNumber) {
    var paymentResponse = {};

    var settings = getPaymentAjaxSettings(productNumber);
    $.ajax(settings)
        .done(function (resp) {
            console.log("complete : " + JSON.stringify(resp));
            paymentResponse = resp;
        })
        .fail(function (e) {
            console.log("ERROR: ", e);
            alert("Connection error!");
        });

    return paymentResponse;
}

function getPaymentAjaxSettings(productNumber) {
    var paymentReqJson = getPaymentInitiationRequestJson(productNumber);
    var headers = getRequestHeaders();
    var xs2aUrl = configs.localhost;

    return {
        async: false,
        crossDomain: true,
        url: xs2aUrl,
        method: "POST",
        headers: headers,
        processData: false,
        data: paymentReqJson,
    };
}

function getPaymentInitiationRequestJson(productNumber) {
    var formObject = {};

    var debtorAccount = {};
    var creditorAccount = {};
    var instructedAmount = {};

    debtorAccount.iban = $("#debtorIban").val();
    debtorAccount.currency = $("#debtorCurrency").val();

    creditorAccount.iban = $("#creditorIban").val();
    creditorAccount.currency = $("#creditorCurrency").val();

    instructedAmount.amount = $("#amount" + productNumber).val();
    instructedAmount.currency = $("#currency" + productNumber).val();

    formObject.debtorAccount = debtorAccount;
    formObject.creditorAccount = creditorAccount;
    formObject.instructedAmount = instructedAmount;

    formObject.ultimateDebtor = $("#debtorName").val();
    formObject.creditorName = $("#creditorName").val();
    formObject.ultimateCreditor = $("#creditorName").val();
    formObject.remittanceInformationUnstructured = $(
        "#productName" + productNumber
    ).val();

    var dateTime = new Date();
    dateTime.setDate(dateTime.getDate() + 1);
    var formattedDateTimeString = dateTime.toISOString();

    var formattedDate = formattedDateTimeString.substring(10, 0);
    var formattedDateTime = formattedDateTimeString.substring(23, 0);

    formObject.requestedExecutionDate = formattedDate;

    return JSON.stringify(formObject);
}

function getRequestHeaders() {
    var headers = {};

    headers["PSU-ID"] = "anton.brueckner";
    headers["TPP-Explicit-Authorisation-Preferred"] = "false";
    headers["TPP-Redirect-Preferred"] = "true";
    headers["TPP-Redirect-URI"] = "https://google.com";
    headers["tpp-transaction-id"] = "16d40f49-a110-4344-a949-f99828ae13c9";
    headers["X-Request-ID"] = "703d6582-334b-45d2-8fdb-1854d1e35c7d";
    headers["PSU-IP-Address"] = "95.67.106.182";
    headers["content-type"] = "application/json";

    return headers;
}
