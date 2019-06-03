import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ErrorHandler, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {FileUploadModule} from 'ng2-file-upload';
import {AppRoutingModule} from './app-routing.module';

import {AppComponent} from './app.component';
import {DocumentUploadComponent} from './commons/document-upload/document-upload.component';
import {FooterComponent} from './commons/footer/footer.component';
import {NavbarComponent} from './commons/navbar/navbar.component';
import {SidebarComponent} from './commons/sidebar/sidebar.component';
import {AccountDetailComponent} from './components/account-detail/account-detail.component';
import {AccountListComponent} from './components/account-list/account-list.component';
import {AccountComponent} from './components/account/account.component';
import {LoginComponent} from './components/auth/login/login.component';
import {RegisterComponent} from './components/auth/register/register.component';
import {CashDepositComponent} from './components/cash-deposit/cash-deposit.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {NotFoundComponent} from './components/not-found/not-found.component';
import {UserCreateComponent} from './components/users/user-create/user-create.component';
import {UserDetailsComponent} from './components/users/user-details/user-details.component';
import {UsersComponent} from './components/users/users.component';
import {AuthGuard} from './guards/auth.guard';
import {AuthInterceptor} from './interceptors/auth-interceptor';
import {UploadFileComponent} from './uploadFile/uploadFile.component';
import {TestDataGenerationComponent} from "./components/testDataGeneration/test-data-generation.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {InfoModule} from "./commons/info/info.module";
import {CertificateComponent} from "./components/auth/certificate/certificate.component";
import {IconModule} from "./commons/icon/icon.module";
import {NgHttpLoaderModule} from 'ng-http-loader';
import {FilterPipeModule} from "ngx-filter-pipe";
import {GlobalErrorsHandler} from "./interceptors/global-errors-handler";

@NgModule({
    declarations: [
        AppComponent,
        NavbarComponent,
        SidebarComponent,
        FooterComponent,
        DashboardComponent,
        LoginComponent,
        RegisterComponent,
        AccountListComponent,
        AccountDetailComponent,
        NotFoundComponent,
        CashDepositComponent,
        UsersComponent,
        UserDetailsComponent,
        UserCreateComponent,
        AccountComponent,
        UploadFileComponent,
        DocumentUploadComponent,
        TestDataGenerationComponent,
        CertificateComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        IconModule,
        InfoModule,
        BrowserAnimationsModule,
        FileUploadModule,
        FilterPipeModule,
        NgHttpLoaderModule.forRoot()
    ],
    providers: [
        AuthGuard,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: ErrorHandler,
            useClass: GlobalErrorsHandler
        }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
