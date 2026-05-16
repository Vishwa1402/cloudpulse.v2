import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private costsSubject = new Subject<any>();

  public costs$ = this.costsSubject.asObservable();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.client.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      this.client.subscribe('/topic/costs', (message: Message) => {
        if (message.body) {
          const payload = JSON.parse(message.body);
          this.costsSubject.next(payload);
        }
      });
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };
  }

  public connect() {
    this.client.activate();
  }

  public disconnect() {
    if (this.client.active) {
      this.client.deactivate();
    }
  }
}
