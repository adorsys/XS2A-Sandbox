import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { NavbarComponent } from '../../common/navbar/navbar.component';
import { AuthService } from '../../common/services/auth.service';
import { SidebarComponent } from '../../common/sidebar/sidebar.component';
import { LoginComponent } from '../login/login.component';
import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
    let component: DashboardComponent;
    let fixture: ComponentFixture<DashboardComponent>;
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn', 'logout']);

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule.withRoutes( [{ path: 'logout', component: LoginComponent }]),
                HttpClientTestingModule,
                ReactiveFormsModule
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
    });
});
