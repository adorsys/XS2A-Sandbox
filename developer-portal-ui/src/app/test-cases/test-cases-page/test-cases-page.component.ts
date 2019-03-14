import { Component, OnInit } from '@angular/core';

import { LanguageService } from '../../common/services/language.service';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { ConfigService } from '../../common/services/config.service';
import { Language } from '../../../models/language';

@Component({
  selector: 'sb-test-cases-page',
  templateUrl: './test-cases-page.component.html',
  styleUrls: ['./test-cases-page.component.scss'],
})
export class TestCasesPageComponent implements OnInit {
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
      return this.config.contentUrlsDe.testcases;
    }
    return this.config.contentUrlsEn.testcases;
  }
}
