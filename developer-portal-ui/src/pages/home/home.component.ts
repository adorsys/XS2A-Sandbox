import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, AfterViewInit {
  slides = [];
  today = +new Date();
  productHistory = [];

  constructor(private router: Router) {
    this.init();
  }

  init() {
    this.slides = [
      {
        title: 'Plug and Play',
        description: 'Ready to use solution, which can be easily installed',
        photoUrl: '../assets/icons/plug&play.png',
        author: 'by John Doe, Position',
      },
      {
        title: 'Detailed Documentation',
        description: 'Well described documentation on a developer portal',
        photoUrl: '../assets/icons/developer.png',
        author: 'by Test Name, Front',
      },
      {
        title: 'Reliable test options',
        description: 'TPP-Test certificate generation tool and NISP Tests',
        photoUrl: '../assets/icons/tested.png',
        author: 'by Mark Travis, Back',
      },
      {
        title: 'Customizable User Interface',
        description: 'TPP-UI for users and account management on the bank side',
        photoUrl: '../assets/icons/customized.png',
        author: 'by Mark Travis, Back',
      },
      {
        title: 'Customer focused',
        description:
          'Client-oriented approach, flexible solution for customer needs and customer support',
        photoUrl: '../assets/icons/user-centered.png',
        author: 'by Mark Travis, Back',
      },
      {
        title: 'Realistic Simulation',
        description: 'Real bank system emulations with embedded XS2A API',
        photoUrl: '../assets/icons/prototyp.png',
        author: 'by Mark Travis, Back',
      },
      {
        title: 'Wide range of services',
        description: 'Accounting Service, Security Service and Payment Service',
        photoUrl: '../assets/icons/service.png',
        author: 'by Mark Travis, Back',
      },
      {
        title: 'Open source',
        description:
          'Open source project, available on GitHub and constantly updated',
        photoUrl: '../assets/icons/open_source.png',
        author: 'by Mark Travis, Back',
      },
    ];
    this.productHistory = [
      {
        title: 'Project Kick-Off with two Team Members',
        date: 1550181600000,
        text: '',
        isToday: false,
        orderNumber: 0,
      },
      {
        title: 'PreDemo of MVP 1.0',
        date: 1553724000000,
        text: 'Embedded approach: PIS, AIS, PIIS',
        isToday: false,
        orderNumber: 1,
      },
      {
        title: 'DEMO of MVP 1.0',
        date: 1554066000000,
        text: 'Embedded and redirected approaches',
        isToday: false,
        orderNumber: 2,
      },
      {
        title: 'Release of XS2ASandbox ver 1.0',
        date: 1556658000000,
        text:
          'Support of Redirect and Embedded approach: TPP-UI, Online Banking',
        isToday: false,
        orderNumber: 3,
      },
      {
        title: 'Release ver 1.1',
        date: 1559336400000,
        text: 'Multilevel SCA, Multicurrency accounts',
        isToday: false,
        orderNumber: 4,
      },
    ];
  }

  checkTodayDay(date) {
    if (date > this.today) {
      return true;
    }
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

  ngOnInit() {
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
      for (let i = 0; i < this.productHistory.length; i++) {
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
}
