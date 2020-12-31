import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {combineLatest, Subject} from 'rxjs';

import {InfoService} from '../../../commons/info/info.service';
import {AuthService} from '../../../services/auth.service';
import {CustomizeService} from '../../../services/customize.service';
import {SettingsService} from '../../../services/settings.service';
import {
  TppIdPatterns,
  TppIdStructure,
  TppIdType,
} from '../../../models/tpp-id-structure.model';
import {CountryService} from '../../../services/country.service';
import {User} from '../../../models/user.model';
import {TppUserService} from '../../../services/tpp.user.service';
import {PageNavigationService} from '../../../services/page-navigation.service';
import {TestDataGenerationService} from '../../../services/test.data.generation.service';
import {CertificateDownloadService} from '../../../services/certificate/certificate-download.service';
import {CertificateGenerationService} from '../../../services/certificate/certificate-generation.service';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class RegisterComponent implements OnInit, OnDestroy {
  admin = false;

  userForm: FormGroup;
  certificateValue = {};

  generateCertificate: boolean;
  submitted: boolean;
  errorMessage: string;

  selectedCountry = '';
  countries: Array<object> = [];
  showTppStructureMessage = false;
  tppIdStructure: TppIdStructure = {
    length: 8,
    type: TppIdType.n,
  };

  private unsubscribe$ = new Subject<void>();

  constructor(
    private service: AuthService,
    private certificateGenerationService: CertificateGenerationService,
    private certificateDownloadService: CertificateDownloadService,
    private generationService: TestDataGenerationService,
    private infoService: InfoService,
    private router: Router,
    private formBuilder: FormBuilder,
    private settingsService: SettingsService,
    public customizeService: CustomizeService,
    private pageNavigationService: PageNavigationService,
    private countryService: CountryService,
    private tppUserService: TppUserService
  ) {
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  selectCountry() {
    if (this.userForm.controls['id']) {
      this.userForm.controls['id'].setValue('');
    }
    if (this.userForm.disabled) {
      document.getElementById('emptySelect').remove();
    }

    this.service.getTppIdStructure(this.selectedCountry)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(
        (data) => {
          this.tppIdStructure = data;
          this.changeIdValidators();
          this.userForm.enable();
          this.showTppStructureMessage = true;
        },
        () => this.infoService.openFeedback('Could not get TPP ID structure for this country!')
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

  initializeCountryList() {
    this.countryService.getCountryList()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(
        (data) => {
          this.countries = data;
          this.selectedCountry = '';
        },
        () => this.infoService.openFeedback('Could not download country list!')
      );
  }

  ngOnInit() {
    this.tppUserService.currentTppUser
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((user: User) => (this.admin = user && user.userRoles.includes('SYSTEM')));
    this.initializeCountryList();
    this.initializeRegisterForm();
  }

  getCertificateValue(event) {
    this.certificateValue = event;
  }

  get isCertificateGeneratorEnabled() {
    return this.settingsService.settings.certGenEnabled;
  }

  onSubmit(): void {
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
        this.certificateGenerationService.generate(this.certificateValue),
      ]).pipe(takeUntil(this.unsubscribe$))
        .subscribe(
          (combinedData: any) => {
            const encodedCert = combinedData[1].encodedCert;
            const privateKey = combinedData[1].privateKey;

            this.certificateDownloadService.createZipUrl(encodedCert, privateKey).then((url) => {
              message = `${messageBeginning} been successfully registered and certificate generated. The download will start automatically within the 2 seconds`;
              this.certificateDownloadService.navigateAndGiveFeedback({navigateUrl: navigateUrl, url: url, message: message});
            });
          },
          (data) => {
            this.userForm.reset();
            this.infoService.openFeedback(data.error.message);
          }
        );
    } else {
      this.service.register(this.userForm.value, this.selectedCountry)
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe(
          () => {
            message = `${messageBeginning} been successfully registered.`;
            this.certificateDownloadService.navigateAndGiveFeedback({navigateUrl: navigateUrl, message: message});
          },
          (data) => {
            this.userForm.reset();
            this.infoService.openFeedback(data.error.message);
          }
        );
    }
  }

  generateTppId(countryCode: string) {
    if (countryCode) {
      return this.generationService.generateTppId(countryCode)
        .pipe(takeUntil(this.unsubscribe$))
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

  initializeRegisterForm(): void {
    this.userForm = this.formBuilder.group({
      id: '',
      login: ['', Validators.required],
      email: ['',
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
}
