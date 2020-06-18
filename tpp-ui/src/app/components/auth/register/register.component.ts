import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import JSZip from 'jszip';
import { combineLatest } from 'rxjs';

import { InfoService } from '../../../commons/info/info.service';
import { AuthService } from '../../../services/auth.service';
import { CertGenerationService } from '../../../services/cert-generation.service';
import { CustomizeService } from '../../../services/customize.service';
import { SettingsService } from '../../../services/settings.service';
import {
  TppIdPatterns,
  TppIdStructure,
  TppIdType,
} from '../../../models/tpp-id-structure.model';
import { CountryService } from '../../../services/country.service';
import { User } from '../../../models/user.model';
import { TppUserService } from '../../../services/tpp.user.service';
import { PageNavigationService } from '../../../services/page-navigation.service';
import { TestDataGenerationService } from '../../../services/test.data.generation.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class RegisterComponent implements OnInit {
  admin = false;

  public userForm: FormGroup;
  public certificateValue = {};

  public generateCertificate: boolean;
  public submitted: boolean;
  public errorMessage: string;

  public selectedCountry = '';
  public countries: Array<object> = [];
  public showTppStructureMessage = false;
  public tppIdStructure: TppIdStructure = {
    length: 8,
    type: TppIdType.n,
  };

  constructor(
    private service: AuthService,
    private certGenerationService: CertGenerationService,
    private generationService: TestDataGenerationService,
    private infoService: InfoService,
    private router: Router,
    private formBuilder: FormBuilder,
    private settingsService: SettingsService,
    public customizeService: CustomizeService,
    private pageNavigationService: PageNavigationService,
    private countryService: CountryService,
    private tppUserService: TppUserService
  ) {}

  selectCountry() {
    if (this.userForm.controls['id']) {
      this.userForm.controls['id'].setValue('');
    }
    if (this.userForm.disabled) {
      document.getElementById('emptySelect').remove();
    }

    this.service.getTppIdStructure(this.selectedCountry).subscribe(
      (data) => {
        this.tppIdStructure = data;
        this.changeIdValidators();
        this.userForm.enable();
        this.showTppStructureMessage = true;
      },
      (error) => {
        console.log(error);
        this.infoService.openFeedback(
          'Could not get TPP ID structure for this country!'
        );
      }
    );
  }

  changeIdValidators() {
    this.userForm
      .get('id')
      .setValidators([
        Validators.required,
        Validators.pattern(TppIdPatterns[this.tppIdStructure.type]),
        Validators.minLength(this.tppIdStructure.length),
        Validators.maxLength(this.tppIdStructure.length),
      ]);
  }

  getTppIdTypeName() {
    return TppIdType[this.tppIdStructure.type];
  }

  public initializeCountryList() {
    this.countryService.getCountryList().subscribe(
      (data) => {
        this.countries = data;
        this.selectedCountry = '';
      },
      (error) => {
        console.log(error);
        this.infoService.openFeedback('Could not download country list!');
      }
    );
  }

  ngOnInit() {
    this.tppUserService.currentTppUser.subscribe(
      (user: User) => (this.admin = user && user.userRoles.includes('SYSTEM'))
    );
    this.initializeCountryList();
    this.initializeRegisterForm();
  }

  getCertificateValue(event) {
    this.certificateValue = event;
  }

  get isCertificateGeneratorEnabled() {
    return this.settingsService.settings.certGenEnabled;
  }

  public onSubmit(): void {
    if (this.userForm.invalid || !this.certificateValue) {
      this.submitted = true;
      return;
    }

    let message: string;
    const navigateUrl = this.admin ? '/management' : '/login';
    const messageBeginning = this.admin ? 'New TPP has ' : 'You have';

    if (this.generateCertificate && this.certificateValue) {
      combineLatest([
        this.service.register(this.userForm.value, this.selectedCountry),
        this.certGenerationService.generate(this.certificateValue),
      ]).subscribe(
        (combinedData: any) => {
          const encodedCert = combinedData[1].encodedCert;
          const privateKey = combinedData[1].privateKey;

          this.createZipUrl(encodedCert, privateKey).then((url) => {
            message = `${messageBeginning} been successfully registered and certificate generated. The download will start automatically within the 2 seconds`;
            this.navigateAndGiveFeedback(navigateUrl, url, message);
          });
        },
        (data) => {
          this.userForm.reset();
          this.infoService.openFeedback(data.error.message);
        }
      );
    } else {
      this.service
        .register(this.userForm.value, this.selectedCountry)
        .subscribe(
          () => {
            message = `${messageBeginning} been successfully registered.`;
            this.navigateAndGiveFeedback(navigateUrl, '', message);
          },
          (data) => {
            this.userForm.reset();
            this.infoService.openFeedback(data.error.message);
          }
        );
    }
  }

  public navigateAndGiveFeedback(
    navigateUrl: string,
    downloadUrl: string,
    message: string
  ) {
    this.router.navigate([navigateUrl]).then(() => {
      this.infoService.openFeedback(message);
      if (downloadUrl) {
        setTimeout(
          () => {
            this.downloadFile(downloadUrl);
          },
          2000,
          downloadUrl
        );
      }
    });
  }

  public generateTppId(countryCode: string) {
    if (countryCode) {
      return this.generationService
        .generateTppId(countryCode)
        .subscribe((data) => {
          this.userForm.get('id').setValue(data);
          this.infoService.openFeedback(
            'TPP ID has been successfully generated'
          );
        });
    } else {
      this.infoService.openFeedback(
        'To generate TPP ID you need to select a country'
      );
    }
  }

  public generateZipFile(certBlob, keyBlob): Promise<any> {
    const zip = new JSZip();
    zip.file('certificate.pem', certBlob);
    zip.file('private.key', keyBlob);
    return zip.generateAsync({ type: 'blob' });
  }

  public initializeRegisterForm(): void {
    this.userForm = this.formBuilder.group({
      id: ['', []],
      login: ['', Validators.required],
      email: [
        '',
        [
          Validators.required,
          Validators.pattern(
            new RegExp(
              /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            )
          ),
        ],
      ],
      pin: ['', Validators.required],
    });
    this.userForm.disable();
  }

  public createZipUrl(
    encodedCert: string,
    privateKey: string
  ): Promise<string> {
    const blobCert = new Blob([encodedCert], {
      type: 'text/plain',
    });
    const blobKey = new Blob([privateKey], {
      type: 'text/plain',
    });
    return this.generateZipFile(blobCert, blobKey).then((zip) => {
      return this.createObjectUrl(zip, window);
    });
  }

  public createObjectUrl(zip: any, window: any): string {
    return window.URL.createObjectURL(zip);
  }

  public downloadFile(url: string) {
    const element = document.createElement('a');
    element.setAttribute('href', url);
    element.setAttribute('download', 'tpp_cert.zip');
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  }
}
