import {AfterViewInit, Component, OnInit} from '@angular/core';

import {CustomizeService} from '../../services/customize.service';
import {ContactInfo, Theme} from "../../models/theme.model";
import {LanguageService} from "../../services/language.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, AfterViewInit {
  today = +new Date();
  contactInfo: ContactInfo;
  slides = [
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_1.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_1.CONTENT',
      photoUrl: '../../assets/icons/plug&play.png',
    },
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_2.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_2.CONTENT',
      photoUrl: '../../assets/icons/developer.png',
    },
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_3.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_3.CONTENT',
      photoUrl: '../../assets/icons/tested.png',
    },
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_4.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_4.CONTENT',
      photoUrl: '../../assets/icons/customized.png',
    },
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_5.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_5.CONTENT',
      photoUrl: '../../assets/icons/user-centered.png',
    },
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_6.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_6.CONTENT',
      photoUrl: '../../assets/icons/prototyp.png',
    },
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_7.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_7.CONTENT',
      photoUrl: '../../assets/icons/service.png',
    },
    {
      title: 'HOME.FEATURES.SLIDES.SLIDE_8.TITLE',
      description: 'HOME.FEATURES.SLIDES.SLIDE_8.CONTENT',
      photoUrl: '../../assets/icons/open_source.png',
    },
  ];
  productHistory = [
    {
      title: 'HOME.HISTORY.PRODUCT.POINT_1.TITLE',
      text: 'HOME.HISTORY.PRODUCT.POINT_1.CONTENT',
      date: 1550181600000,
      isToday: false,
      orderNumber: 0,
    },
    {
      title: 'HOME.HISTORY.PRODUCT.POINT_2.TITLE',
      text: 'HOME.HISTORY.PRODUCT.POINT_2.CONTENT',
      date: 1553724000000,
      isToday: false,
      orderNumber: 1,
    },
    {
      title: 'HOME.HISTORY.PRODUCT.POINT_3.TITLE',
      text: 'HOME.HISTORY.PRODUCT.POINT_3.CONTENT',
      date: 1554066000000,
      isToday: false,
      orderNumber: 2,
    },
    {
      title: 'HOME.HISTORY.PRODUCT.POINT_4.TITLE',
      text: 'HOME.HISTORY.PRODUCT.POINT_4.CONTENT',
      date: 1556658000000,
      isToday: false,
      orderNumber: 3,
    },
    {
      title: 'HOME.HISTORY.PRODUCT.POINT_5.TITLE',
      text: 'HOME.HISTORY.PRODUCT.POINT_5.CONTENT',
      date: 1559336400000,
      isToday: false,
      orderNumber: 4,
    },
  ];

  pathToFile = `./assets/i18n/en/home.md`;

  showQuestionsComponent;
  showProductHistory;
  showSlider = true;

  constructor(private languageService: LanguageService,
              private customizeService: CustomizeService) {
    setTimeout(() => { // TODO create single instance of this service and pull data only one time! https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/591
        let theme = this.customizeService.getTheme();
        if (theme.contactInfo.img.indexOf('/') === -1) { // TODO do it in the Customize service https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/591
          theme.contactInfo.img =
            '../assets/UI' +
            (this.customizeService.isCustom() ? '/custom/' : '/') +
            theme.contactInfo.img;
        }

        this.contactInfo = theme.contactInfo;
        const homePageSettings = theme.pagesSettings.homePageSettings;

        if (homePageSettings) {

          this.enableSlider(homePageSettings.showSlider);
          this.enableProductHistory(homePageSettings.showProductHistory);
          this.enableQuestionsComponent(homePageSettings.showQuestionsComponent);
        }
      }, 500
    );
  }

  ngOnInit() {

    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToFile = `./assets/i18n/${data}/home.md`;
      });
  }

  checkTodayDay(date) {
    let isToday = false;
    if (date > this.today) {
      isToday = true;
    }
    return isToday;
  }

  slider() {
    const images = document.querySelectorAll('#slider .slide');
    const buttons = document.querySelectorAll('.button-block button');
    buttons[0]['style'].backgroundColor = '#D8D8D8';
    let currentImg = 0;
    let carouselIntervalId = null;
    // Show only first slide
    images[currentImg]['classList'].add('show');
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].addEventListener('click', () => {
        buttons[currentImg]['style'].backgroundColor = null;
        buttons[i]['style'].backgroundColor = '#D8D8D8';
        // Stop autoplay on button click
        clearInterval(carouselIntervalId);
        images[currentImg]['classList'].remove('show');
        images[i]['classList'].add('show');
        currentImg = i;
      });
    }

    function nextSlide() {
      images[currentImg]['classList'].remove('show');
      buttons[currentImg]['style'].backgroundColor = null;
      currentImg++;
      if (currentImg >= images.length) {
        currentImg = 0;
      }
      images[currentImg]['classList'].add('show');
      buttons[currentImg]['style'].backgroundColor = '#D8D8D8';
    }

    // Start autoplay
    carouselIntervalId = setInterval(nextSlide, 3000);
  }

  setProductHistory() {
    if (this.productHistory[this.productHistory.length - 1].date < this.today) {
      this.productHistory.push({
        title: '',
        date: null,
        text: '',
        isToday: true,
        orderNumber: null,
      });
    } else if (this.productHistory[0].date > this.today) {
      this.productHistory.unshift({
        title: '',
        date: null,
        text: '',
        isToday: true,
        orderNumber: null,
      });
    } else {
      for (let i = 0; i < this.productHistory.length - 1; i++) {
        if (
          this.productHistory[i].date <= this.today &&
          this.productHistory[i + 1].date > this.today
        ) {
          this.productHistory.splice(i + 1, 0, {
            title: '',
            date: null,
            text: '',
            isToday: true,
            orderNumber: null,
          });
          break;
        }
      }
    }
  }

  ngAfterViewInit() {
    this.slider();
  }

  private enableSlider(showSlider: boolean) {
    this.showSlider = !showSlider
      ? showSlider
      : true;
  }

  private enableProductHistory(showProductHistory: boolean) {
    this.showProductHistory = !showProductHistory
      ? showProductHistory
      : true;

    if (this.productHistory) {
      this.setProductHistory();
    }
  }

  private enableQuestionsComponent(showQuestionsComponent: boolean) {
    this.showQuestionsComponent = !showQuestionsComponent
      ? showQuestionsComponent
      : true;
  }
}
