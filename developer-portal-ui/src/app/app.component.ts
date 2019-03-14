import { Component, NgModule } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LanguageService } from './common/services/language.service';

@Component({
  selector: 'sb-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
@NgModule({
  imports: [LanguageService],
})
export class AppComponent {
  constructor(private http: HttpClient) {}
}
