import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";

@Component({
  selector: 'app-error',
  templateUrl: './testing-flows.component.html',
  styleUrls: ['./testing-flows.component.scss'],
})
export class TestingFlowsComponent implements OnInit {
  pathToTestingFlows = `./assets/i18n/en/test-cases/testingFlows.md`;

  constructor(private languageService: LanguageService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToTestingFlows = `./assets/i18n/${data}/test-cases/testingFlows.md`;
      });
  }

}
