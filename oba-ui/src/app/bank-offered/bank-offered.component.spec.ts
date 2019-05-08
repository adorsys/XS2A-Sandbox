import {HttpClientTestingModule} from '@angular/common/http/testing';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';

import {RouterTestingModule} from '@angular/router/testing';
import {URL_PARAMS_PROVIDER} from '../common/constants/constants';
import {ShareDataService} from '../common/services/share-data.service';
import {BankOfferedComponent} from './bank-offered.component';

describe('BankOfferedComponent', () => {
    let component: BankOfferedComponent;
    let fixture: ComponentFixture<BankOfferedComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [
                BankOfferedComponent
            ],
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientTestingModule
            ],
            providers: [
                ShareDataService,
                {provide: URL_PARAMS_PROVIDER, useValue: {}}
            ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(BankOfferedComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('Should check that the button Next is deactivated when the form is invalid', () => {
        const nextBtn = fixture.nativeElement.querySelector('button[type="submit"]');
        expect(nextBtn.disabled).toBeTruthy();
        expect(component.bankOfferedForm.valid).toBeFalsy();
    });

    it('Should check that the contents of tag h2 and css font-weight-light', async(() => {

        const redirectBtn = fixture.debugElement.nativeElement.querySelector('h2');
        expect(redirectBtn.textContent).toContain('Select Bank Accounts');

        const fixtureText = fixture.debugElement.query(By.css('.font-weight-light'));
        expect(fixtureText.nativeElement.textContent)
            .toContain('Please select your Account and confirm with “Next"');
    }));

    it('Should check that the button Next is activated when on option is selected', function () {
        const nextBtn = fixture.nativeElement.querySelector('button[type="submit"]');
        const account = component.bankOfferedForm.controls['accounts'];
        account.setValue('{bank: \'Deutsche Bank\',\n' +
            '            iban: \'DE76 1234 5987 6501 2345 6789 00\',\n' +
            '            bban: \'BICC12345612345678\',\n' +
            '            pan: \'5409050000000000\',\n' +
            '            maskedPan: \'123456xxxxxx1234\',\n' +
            '            msisdn: \'+49 170 1234567\',\n' +
            '            currency: \'EUR\'}');
        fixture.detectChanges();
        expect(nextBtn.disabled).toBeFalsy();
        expect(component.bankOfferedForm.valid).toBeTruthy();

    });
});
