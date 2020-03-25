import {Component} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";
import {CustomizeService} from "../../../../services/customize.service";

@Component({
  selector: 'app-test-values',
  templateUrl: './test-values.component.html',
  styleUrls: ['./test-values.component.scss'],
})
export class TestValuesComponent {
  pathToTestValues = `./assets/content/i18n/en/test-cases/predefinedTestValues.md`;

  constructor(private languageService: LanguageService,
              private customizeService: CustomizeService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToTestValues = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/predefinedTestValues.md`;
      });
  }
}
