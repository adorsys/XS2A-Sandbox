import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";

import {DashboardComponent} from './dashboard.component';
import {NavbarComponent} from "../../commons/navbar/navbar.component";
import {SidebarComponent} from "../../commons/sidebar/sidebar.component";
import {IconModule} from "../../commons/icon/icon.module";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('DashboardComponent', () => {
    let component: DashboardComponent;
    let fixture: ComponentFixture<DashboardComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule,
                HttpClientTestingModule,
                IconModule
            ],
            declarations: [DashboardComponent, NavbarComponent, SidebarComponent]
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
