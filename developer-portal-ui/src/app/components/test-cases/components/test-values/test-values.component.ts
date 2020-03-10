import {Component} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";

@Component({
  selector: 'app-test-values',
  templateUrl: './test-values.component.html',
  styleUrls: ['./test-values.component.scss'],
})
export class TestValuesComponent {
  pathToTestValues = `./assets/i18n/en/test-cases/predefinedTestValues.md`;

  constructor(private languageService: LanguageService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToTestValues = `./assets/i18n/${data}/test-cases/predefinedTestValues.md`;
      });
  }
}
