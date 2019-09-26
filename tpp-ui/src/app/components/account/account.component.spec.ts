import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IconModule } from '../../commons/icon/icon.module';
import { InfoModule } from '../../commons/info/info.module';
import { InfoService } from '../../commons/info/info.service';
import { AccountStatus, AccountType, UsageType } from '../../models/account.model';
import { AccountService } from '../../services/account.service';
import { AccountComponent } from './account.component';
import { ConvertBalancePipe } from '../../pipes/convertBalance.pipe';

describe('AccountComponent', () => {
    let component: AccountComponent;
    let fixture: ComponentFixture<AccountComponent>;
    let accountService: AccountService;
    let infoService: InfoService;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule,
                HttpClientTestingModule,
                InfoModule,
                IconModule
            ],
            declarations: [AccountComponent, ConvertBalancePipe],
            providers: [AccountService, InfoService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        infoService = TestBed.get(InfoService);
        accountService = TestBed.get(AccountService);
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });


    it('should call getAccountReport on ngOnInit', () => {
        let getAccountSpy = spyOn(accountService, 'getAccountReport').and.callThrough();

        component.ngOnInit();

        expect(getAccountSpy).toHaveBeenCalled();
    });

    it('should assign account-report after server call', () => {
        let accountReport = {
            details: {
                id: 'XXXXXX',
                iban: 'DE35653635635663',
                bban: 'BBBAN',
                pan: 'pan',
                maskedPan: 'maskedPan',
                currency: 'EUR',
                msisdn: 'MSISDN',
                name: 'Pupkin',
                product: 'Deposit',
                accountType: AccountType.CASH,
                accountStatus: AccountStatus.ENABLED,
                bic: 'BIChgdgd',
                usageType: UsageType.PRIV,
                details: '',
                linkedAccounts: '',
                balances: []
            },
            accesses: [{
                userLogin: 'xxxxx',
                scaWeight: 4,
            }]
        };

        spyOn(accountService, 'getAccountReport').and.returnValue(of(accountReport));
        component.getAccountReport();
        expect(component.getAccountReport).not.toBeUndefined();
    });
});
