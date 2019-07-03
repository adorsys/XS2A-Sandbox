import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  lang = 'en';

  constructor(private translateService: TranslateService) {}

  initializeTranslation() {
    this.translateService.addLangs(['en', 'ua', 'es', 'de']);
    this.translateService.setDefaultLang(this.lang);
    this.translateService.use(this.lang);
  }

  setLang(lang: string) {
    this.translateService.use(lang);
    this.lang = lang;
  }

  getLang() {
    return this.lang;
  }
}
