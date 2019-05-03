import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.scss'],
})
export class FaqComponent implements OnInit {
  jsonData1 = {};
  jsonData2 = {};
  jsonData3 = {};
  jsonData4 = {};

  constructor() {
    this.init();
  }

  init() {
    this.jsonData1 = {
      timestamp: 1549441548991,
      status: 403,
      error: 'Forbidden',
      message: 'You don"t have access to this resource',
      path: '/v1/consents',
    };
    this.jsonData2 = {
      tppMessages: [
        {
          category: 'ERROR',
          code: 'FORMAT_ERROR',
          path: null,
          text:
            'Format of certain request fields are not matching the XS2A requirements.',
        },
      ],
      _links: null,
    };
    this.jsonData3 = {
      tppMessages: [
        {
          category: 'ERROR',
          code: 'CONSENT_EXPIRED',
          path: null,
          text:
            'The consent was created by this TPP but has expired and needs to be renewed',
        },
      ],
      _links: null,
    };
    this.jsonData4 = {
      tppMessages: [
        {
          category: 'ERROR',
          code: 'CONSENT_INVALID',
          path: null,
          text:
            'The consent was created by this TPP but is not valid for the addressed service/resource',
        },
      ],
      _links: null,
    };
  }

  ngOnInit() {}
}
