import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountDetailsComponent} from './account-details.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AuthService} from "../../common/services/auth.service";

describe('AccountDetailsComponent', () => {
    let component: AccountDetailsComponent;
    let fixture: ComponentFixture<AccountDetailsComponent>;
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthorizedUser', 'isLoggedIn', 'logout']);

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [AccountDetailsComponent],
            imports: [RouterTestingModule, HttpClientTestingModule],
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
