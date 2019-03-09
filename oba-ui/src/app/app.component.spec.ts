import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {RouterOutlet} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';

import {AccountDetailsComponent} from './account-details/account-details.component';
import {AppComponent} from './app.component';

describe('AppComponent', () => {
    let app: AppComponent;
    let fixture: ComponentFixture<AppComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule
            ],
            declarations: [
                AppComponent,
                AccountDetailsComponent
            ],
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AppComponent);
        app = fixture.componentInstance;
    });

    it('should create the app', async(() => {
        expect(app).toBeTruthy();
    }));

    it('Should include the < router-outlet >', async(() => {
        fixture.detectChanges();
        const debugElement = fixture.debugElement.query(By.directive(RouterOutlet));
        expect(debugElement).not.toBeNull();
    }));

});
