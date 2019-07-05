import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";

import {DashboardComponent} from './dashboard.component';
import {NavbarComponent} from "../../commons/navbar/navbar.component";
import {SidebarComponent} from "../../commons/sidebar/sidebar.component";
import {IconModule} from "../../commons/icon/icon.module";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoginComponent} from "../auth/login/login.component";
import {ReactiveFormsModule} from "@angular/forms";
import {AuthService} from "../../services/auth.service";

describe('DashboardComponent', () => {
    let component: DashboardComponent;
    let fixture: ComponentFixture<DashboardComponent>;
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn', 'logout']);

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule.withRoutes( [{ path: 'logout', component: LoginComponent }]),
                HttpClientTestingModule,
                ReactiveFormsModule,
                IconModule
            ],
            providers: [TestBed.overrideProvider(AuthService, {useValue: authServiceSpy})],
            declarations: [DashboardComponent, NavbarComponent, SidebarComponent, LoginComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(DashboardComponent);
        component = fixture.componentInstance;
        authServiceSpy.isLoggedIn.and.returnValue(true);
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
        expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
    });
});
