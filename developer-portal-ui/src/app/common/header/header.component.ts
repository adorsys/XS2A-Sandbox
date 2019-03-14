import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../services/language.service';
import { Language } from '../../../models/language';
import { Observable } from 'rxjs';
import { ConfigService } from '../services/config.service';
import { Config } from '../../../models/config';

@Component({
  selector: 'sb-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  public selectedLanguage$: Observable<Language>;
  public languages = Language;
  public config: Config;

  constructor(
    private languageService: LanguageService,
    private configService: ConfigService
  ) {
    this.selectedLanguage$ = languageService.getLanguage$();
    this.config = configService.getConfig();
  }

  ngOnInit() {}

  onSelectLanguage(selectedLanguage: Language) {
    this.languageService.setLanguage(selectedLanguage);
  }
}
