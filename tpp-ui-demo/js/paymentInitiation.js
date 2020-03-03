function getPaymentRedirectLink(productNumber) {
    var paymentResp = sendPaymentRequestAndGetResponse(productNumber);

    if ((paymentResp["_links"] !== undefined)
        && (paymentResp["_links"].scaRedirect !== undefined)) {

        var redirectLink = paymentResp["_links"].scaRedirect.href;
        console.log("redirectLink : " + redirectLink);

        window.location = redirectLink;
    }
}