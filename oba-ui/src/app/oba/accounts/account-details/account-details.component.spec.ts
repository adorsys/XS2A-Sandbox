import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import {
  NgbDatepickerModule,
  NgbPaginationModule,
} from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { AuthService } from '../../../common/services/auth.service';
import { AccountDetailsComponent } from './account-details.component';
import { OnlineBankingService } from '../../../common/services/online-banking.service';
import { ConvertBalancePipe } from '../../../pipes/convertBalance.pipe';

describe('AccountDetailsComponent', () => {
  let component: AccountDetailsComponent;
  let fixture: ComponentFixture<AccountDetailsComponent>;
  let authService: AuthService;
  let onlineBankingService: OnlineBankingService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AccountDetailsComponent, ConvertBalancePipe],
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        NgbDatepickerModule,
        NgbPaginationModule,
      ],
      providers: [AuthService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountDetailsComponent);
    component = fixture.componentInstance;
    authService = TestBed.get(AuthService);
    onlineBankingService = TestBed.get(OnlineBankingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get the account detail', () => {
    const mockAccount = {
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
    };
    const accountSpy = spyOn(
      onlineBankingService,
      'getAccount'
    ).and.returnValue(of({ mockAccount }));
    component.getAccountDetail();
    expect(accountSpy).toHaveBeenCalled();
  });

  it('should get the Transactions', () => {
    const mockResponse = {};
    const transactionsSpy = spyOn(
      onlineBankingService,
      'getTransactions'
    ).and.returnValue(of({ mockResponse }));
    component.getTransactions(5, 10);
    expect(transactionsSpy).toHaveBeenCalled();
  });

  it('should change the page', () => {
    const config = {
      itemsPerPage: 10,
      currentPage: 1,
      totalItems: 0,
      maxSize: 7,
    };
    const transactionsSpy = spyOn(component, 'getTransactions');
    component.pageChange(10);
    expect(transactionsSpy).toHaveBeenCalledWith(10, 10);
  });
});
