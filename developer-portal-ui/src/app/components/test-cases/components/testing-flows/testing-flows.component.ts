import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";
import {CustomizeService} from "../../../../services/customize.service";

@Component({
  selector: 'app-error',
  templateUrl: './testing-flows.component.html',
  styleUrls: ['./testing-flows.component.scss'],
})
export class TestingFlowsComponent implements OnInit {
  pathToTestingFlows = `./assets/content/i18n/en/test-cases/testingFlows.md`;

  constructor(private languageService: LanguageService,
              private customizeService: CustomizeService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToTestingFlows = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/testingFlows.md`;
      });
  }

}
