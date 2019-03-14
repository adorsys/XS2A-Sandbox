import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../common/services/language.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConfigService } from '../../common/services/config.service';
import { Language } from '../../../models/language';

@Component({
  selector: 'sb-developer-portal-page',
  templateUrl: './developer-portal-page.component.html',
  styleUrls: ['./developer-portal-page.component.scss'],
})
export class DeveloperPortalPageComponent implements OnInit {
  public localizedContent$: Observable<string>;
  private config;

  constructor(
    private languageService: LanguageService,
    private configService: ConfigService
  ) {
    this.config = configService.getConfig();
  }

  ngOnInit() {
    this.localizedContent$ = this.languageService
      .getLanguage$()
      .pipe(map(lang => this.getMarkdownFiles(lang)));
  }

  getMarkdownFiles(lang: Language): string {
    if (lang === Language.de) {
      return this.config.contentUrlsDe.portal;
    }
    return this.config.contentUrlsEn.portal;
  }
}
