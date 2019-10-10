import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthorizeComponent } from './authorize/authorize.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NotFoundModule} from "../not-found/not-found.module";
import {AuthRoutingModule} from "./auth-routing.module";



@NgModule({
  declarations: [AuthorizeComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    AuthRoutingModule,
    NotFoundModule
  ]
})
export class AuthModule { }
