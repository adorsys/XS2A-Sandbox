import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../services/account.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-cash-deposit',
    templateUrl: './cash-deposit.component.html',
    styleUrls: ['./cash-deposit.component.scss']
})
export class CashDepositComponent implements OnInit {

    cashDepositForm: FormGroup;

    submitted: boolean;
    accountId: string;
    errorMessage: string;

    constructor(private accountService: AccountService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private formBuilder: FormBuilder,) {
    }

    ngOnInit() {
        this.accountId = this.activatedRoute.snapshot.paramMap.get('id');
        this.cashDepositForm = this.formBuilder.group({
          currency: [{value: '', disabled: true}, Validators.required],
          amount: ['', [Validators.required, Validators.min(0)]]
        });
        this.accountService.getAccount(this.accountId).subscribe(
          data => this.cashDepositForm.get('currency').setValue(data['currency']));
    }

    onSubmit() {
        this.submitted = true;
        if (this.cashDepositForm.invalid) {
            return;
        }

        this.accountService.depositCash(this.accountId, this.cashDepositForm.getRawValue())
            .subscribe(
                () => this.router.navigate(['/accounts']),
                error => {
                    if (typeof error.error === 'object') {
                        this.errorMessage = error.error.status + ' ' + error.error.error + ': ' + error.error.message;
                    } else {
                        this.errorMessage = error.status + ' ' + error.error
                    }
                });
    }
}
