import {Component, OnInit} from '@angular/core';
import {Subscription} from "rxjs/index";
import {PaymentTO} from "../../api/models/payment-to";
import {OnlineBankingService} from "../../common/services/online-banking.service";
import {InfoService} from "../../common/info/info.service";

@Component({
    selector: 'app-periodic-payments',
    templateUrl: './periodic-payments.component.html',
    styleUrls: ['./periodic-payments.component.scss']
})
export class PeriodicPaymentsComponent implements OnInit {

    payments: PaymentTO[];
    private subscriptions: Subscription[] = [];

    constructor(private infoService: InfoService,
                private onlineBankingService: OnlineBankingService ) {
    }

    ngOnInit() {
        this.getPeriodicPayments();
    }

    getPeriodicPayments() {
        this.onlineBankingService.getPayments().subscribe(payments => {
            this.payments = payments;
        });
    }
}
