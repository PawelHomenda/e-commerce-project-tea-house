import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { HomeComponent } from "./home/home";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [CommonModule,RouterOutlet, HomeComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  protected readonly title = signal('browser-interface');
}
