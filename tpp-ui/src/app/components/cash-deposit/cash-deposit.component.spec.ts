import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { AccountService } from '../../services/account.service';
import { CashDepositComponent } from './cash-deposit.component';

describe('CashDepositComponent', () => {
    let component: CashDepositComponent;
    let fixture: ComponentFixture<CashDepositComponent>;
    let accountService: AccountService;
    let router: Router;
    let activate: ActivatedRoute;
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
        activate = TestBed.get(ActivatedRoute);
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load the accounts on ngOnInit', () => {
        const spyRoute = spyOn(activate.snapshot.paramMap, 'get')
        spyRoute.and.returnValue('id')
        component.cashDepositForm.setValue({
            currency: 'EUR',
            amount: '50'
        })
        let accountSpy = spyOn(accountService, 'getAccounts').and.returnValue(
            of({data: component.cashDepositForm.controls['currency'].setValue('EUR')}));
        component.ngOnInit();

        expect(spyRoute).toHaveBeenCalled;
        expect(accountSpy).toHaveBeenCalled;
    });

    it('should deposit cash when cashDepositForm is valid and submitted', () => {
        expect(component.submitted).toBeFalsy();
        component.onSubmit();
        expect(component.cashDepositForm.invalid).toBeTruthy();

        // set valid values for cashDepositForm
        component.cashDepositForm.controls['currency'].setValue('EUR');
        component.cashDepositForm.controls['amount'].setValue('50');

        // cashDepositForm submit
        const sampleResponse = {value: 'sample response'};
        let depositCashSpy = spyOn(accountService, 'depositCash').and.callFake(() => of(sampleResponse));
        let navigateSpy = spyOn(router, 'navigate');
        component.onSubmit();
        expect(component.submitted).toBeTruthy();
        expect(component.cashDepositForm.valid).toBeTruthy();
        expect(depositCashSpy).toHaveBeenCalled();
        expect(navigateSpy).toHaveBeenCalledWith(['/accounts']);
    });

    it('should throw error onSubmit', () => {
        let depositCashSpy = spyOn(accountService, 'depositCash').and.returnValue(throwError({status: 404}));

        // set valid values for cashDepositForm
        component.cashDepositForm.controls['currency'].setValue('EUR');
        component.cashDepositForm.controls['amount'].setValue('50');
        component.onSubmit();
        expect(depositCashSpy).toHaveBeenCalled();
        expect(component.errorMessage).toBeTruthy();
    });
});
