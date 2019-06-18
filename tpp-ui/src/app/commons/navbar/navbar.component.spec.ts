import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarComponent } from './navbar.component';
import {RouterTestingModule} from "@angular/router/testing";
import {IconModule} from "../icon/icon.module";
import {AuthService} from "../../services/auth.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoginComponent} from "../../components/auth/login/login.component";
import {ReactiveFormsModule} from "@angular/forms";
import {Observable} from "rxjs";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";

describe('NavbarComponent', () => {
    let component: NavbarComponent;
    let fixture: ComponentFixture<NavbarComponent>;
    let authService: AuthService;
    let router: Router;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule.withRoutes( [{ path: 'logout', component: LoginComponent }]),
                HttpClientTestingModule,
                ReactiveFormsModule,
                IconModule,
            ],
            providers: [AuthService],
            declarations: [ NavbarComponent, LoginComponent ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(NavbarComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        authService = TestBed.get(AuthService);
        router = TestBed.get(Router);

        spyOn(authService, 'logout').and.callThrough();
        component.onLogout();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
