import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';

import { AuthService } from '../../../common/services/auth.service';
import { AccountDetailsComponent } from './account-details.component';

describe('AccountDetailsComponent', () => {
    let component: AccountDetailsComponent;
    let fixture: ComponentFixture<AccountDetailsComponent>;
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthorizedUser', 'isLoggedIn', 'logout']);

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [AccountDetailsComponent],
            imports: [RouterTestingModule, HttpClientTestingModule, ReactiveFormsModule, NgbDatepickerModule],
            providers: [TestBed.overrideProvider(AuthService, {useValue: authServiceSpy})]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountDetailsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

});
