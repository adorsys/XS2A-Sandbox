import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CashDepositComponent} from './cash-deposit.component';
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AccountService} from "../../services/account.service";
import {Observable} from "rxjs";
import {Router} from "@angular/router";

describe('CashDepositComponent', () => {
    let component: CashDepositComponent;
    let fixture: ComponentFixture<CashDepositComponent>;
    let accountService: AccountService;
    let router: Router;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientTestingModule,
                RouterTestingModule.withRoutes([])
            ],
            providers: [
                AccountService
            ],
            declarations: [CashDepositComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(CashDepositComponent);
        component = fixture.componentInstance;
        accountService = TestBed.get(AccountService);
        router = TestBed.get(Router);
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should deposit cash when cashDepositForm is valid and submitted', () => {
        expect(component.submitted).toBeFalsy();

        // set valid values for cashDepositForm
        component.cashDepositForm.controls['currency'].setValue('EUR');
        component.cashDepositForm.controls['amount'].setValue('50');

        // cashDepositForm submit
        const sampleResponse = {value: 'sample response'};
        let depositCashSpy = spyOn(accountService, 'depositCash').and.callFake(() => Observable.of(sampleResponse));
        let navigateSpy = spyOn(router, 'navigate');
        component.onSubmit();
        expect(component.submitted).toBeTruthy();
        expect(component.cashDepositForm.valid).toBeTruthy();
        expect(depositCashSpy).toHaveBeenCalled();
        expect(navigateSpy).toHaveBeenCalledWith(['/accounts']);
    });
});
