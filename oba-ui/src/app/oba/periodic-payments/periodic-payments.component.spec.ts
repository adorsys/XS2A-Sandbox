import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicPaymentsComponent } from './periodic-payments.component';
import {ClipboardModule} from "ngx-clipboard";
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {InfoModule} from "../../common/info/info.module";

describe('PeriodicPaymentsComponent', () => {
    let component: PeriodicPaymentsComponent;
    let fixture: ComponentFixture<PeriodicPaymentsComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [ReactiveFormsModule, RouterTestingModule, InfoModule, ClipboardModule],
            declarations: [ PeriodicPaymentsComponent ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(PeriodicPaymentsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
