import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountAccessManagementComponent} from './account-access-management.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AccountService} from "../../services/account.service";
import {RouterTestingModule} from "@angular/router/testing";

describe('AccountAccessManagementComponent', () => {
    let component: AccountAccessManagementComponent;
    let fixture: ComponentFixture<AccountAccessManagementComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                HttpClientTestingModule,
                RouterTestingModule,
                FormsModule,
            ],
            declarations: [AccountAccessManagementComponent],
            providers: [AccountService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountAccessManagementComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
