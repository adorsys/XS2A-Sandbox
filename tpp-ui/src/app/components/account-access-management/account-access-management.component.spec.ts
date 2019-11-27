import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountAccessManagementComponent} from './account-access-management.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AccountService} from "../../services/account.service";
import {RouterTestingModule} from "@angular/router/testing";
import {InfoModule} from "../../commons/info/info.module";
import {InfoService} from "../../commons/info/info.service";
import {NgbTypeaheadModule} from "@ng-bootstrap/ng-bootstrap";

describe('AccountAccessManagementComponent', () => {
    let component: AccountAccessManagementComponent;
    let fixture: ComponentFixture<AccountAccessManagementComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                HttpClientTestingModule,
                RouterTestingModule,
                NgbTypeaheadModule,
                InfoModule,
                FormsModule,
            ],
            declarations: [AccountAccessManagementComponent],
            providers: [AccountService, InfoService]
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
