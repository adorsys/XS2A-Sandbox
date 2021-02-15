import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { AuthService } from '../../common/services/auth.service';
import { AccountsComponent } from './accounts.component';
import { of } from 'rxjs';
describe('AccountsComponent', () => {
  let component: AccountsComponent;
  let fixture: ComponentFixture<AccountsComponent>;
  let onlineBankingService: OnlineBankingService;
  const authServiceSpy = jasmine.createSpyObj('AuthService', [
    'getAuthorizedUser',
    'isLoggedIn',
    'logout',
  ]);

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AccountsComponent],
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [
          TestBed.overrideProvider(AuthService, { useValue: authServiceSpy }),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsComponent);
    component = fixture.componentInstance;
    onlineBankingService = TestBed.get(OnlineBankingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get the accounts list', () => {
    let mockAccounts = [
      {
        id: '123456',
        iban: 'DE35653635635663',
        bban: 'BBBAN',
        pan: 'pan',
        maskedPan: 'maskedPan',
        currency: 'EUR',
        msisdn: 'MSISDN',
        name: 'Pupkin',
        product: 'Deposit',
        accountType: 'CASH',
        accountStatus: 'ENABLED',
        bic: 'BIChgdgd',
        usageType: 'PRIV',
        details: '',
        linkedAccounts: '',
        balances: [],
      },
    ];
    let accountsSpy = spyOn(
      onlineBankingService,
      'getAccounts'
    ).and.returnValue(
      of<any>({ mockAccounts })
    );
    component.getAccounts();
    expect(accountsSpy).toHaveBeenCalled();
  });
});
