import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";

import {DashboardComponent} from './dashboard.component';
import {NavbarComponent} from "../../commons/navbar/navbar.component";
import {SidebarComponent} from "../../commons/sidebar/sidebar.component";
import {IconModule} from "../../commons/icon/icon.module";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoginComponent} from "../auth/login/login.component";
import {ReactiveFormsModule} from "@angular/forms";

describe('DashboardComponent', () => {
    let component: DashboardComponent;
    let fixture: ComponentFixture<DashboardComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule.withRoutes( [{ path: 'logout', component: LoginComponent }]),
                HttpClientTestingModule,
                ReactiveFormsModule,
                IconModule
            ],
            declarations: [DashboardComponent, NavbarComponent, SidebarComponent, LoginComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(DashboardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
