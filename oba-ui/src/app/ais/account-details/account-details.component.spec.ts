import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ShareDataService } from '../../common/services/share-data.service';
import { AccountDetailsComponent } from './account-details.component';

describe('AccountDetailsComponent', () => {
  let component: AccountDetailsComponent;
  let fixture: ComponentFixture<AccountDetailsComponent>;
  let shareDataService: ShareDataService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AccountDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountDetailsComponent);
    shareDataService = TestBed.get(ShareDataService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return empty when no auth response for accounts', () => {
    component.authResponse = null;
    const result = component.accounts;
    expect(result).toEqual([]);
  });

  it('should return accounts', () => {
    component.authResponse = {
      consent: {
        access: {
          accounts: [
            'DE12 1234 5678 9000 0005',
            'DE12 1234 5678 9000 0003',
            'DE12 1234 5678 9000 0002',
            'DE12 1234 5678 9000 0004',
            'DE12 1234 5678 9000 0000',
            'DE12 1234 5678 9000 0001',
          ],
        },
        frequencyPerDay: 10,
        id: '12345',
        recurringIndicator: false,
        tppId: '12345',
        userId: '54321',
        validUntil: 'valid',
      },
    };
    const result = component.accounts;
    expect(result).toEqual([
      'DE12 1234 5678 9000 0000',
      'DE12 1234 5678 9000 0001',
      'DE12 1234 5678 9000 0002',
      'DE12 1234 5678 9000 0003',
      'DE12 1234 5678 9000 0004',
      'DE12 1234 5678 9000 0005',
    ]);
  });

  it('should return empty when no auth response for balances', () => {
    component.authResponse = null;
    const result = component.balances;
    expect(result).toEqual([]);
  });

  it('should return  balances', () => {
    component.authResponse = {
      consent: {
        access: {
          balances: ['a', 'c', 'b', 'e', 'd'],
        },
        frequencyPerDay: 10,
        id: '12345',
        recurringIndicator: false,
        tppId: '12345',
        userId: '54321',
        validUntil: 'valid',
      },
    };
    const result = component.balances;
    expect(result).toEqual(['a', 'b', 'c', 'd', 'e']);
  });

  it('should return empty when no auth response for transactions', () => {
    component.authResponse = null;
    const result = component.transactions;
    expect(result).toEqual([]);
  });

  it('should return transactions', () => {
    component.authResponse = {
      consent: {
        access: {
          transactions: ['a', 'c', 'b', 'e', 'd'],
        },
        frequencyPerDay: 10,
        id: '12345',
        recurringIndicator: false,
        tppId: '12345',
        userId: '54321',
        validUntil: 'valid',
      },
    };
    const result = component.transactions;
    expect(result).toEqual(['a', 'b', 'c', 'd', 'e']);
  });
});
