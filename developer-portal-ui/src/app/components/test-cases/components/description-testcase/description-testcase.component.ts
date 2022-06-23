import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../../../services/language.service';
import { CustomizeService } from '../../../../services/customize.service';

@Component({
  selector: 'app-description-testcase',
  templateUrl: './description-testcase.component.html',
  styleUrls: ['./description-testcase.component.scss'],
})
export class DescriptionTestcaseComponent implements OnInit {
  pathToHeadTestCases = `./assets/content/i18n/en/test-cases/headTestCases.md`;

  constructor(private languageService: LanguageService, private customizeService: CustomizeService) {}

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToHeadTestCases = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/headTestCases.md`;
    });
  }
}
