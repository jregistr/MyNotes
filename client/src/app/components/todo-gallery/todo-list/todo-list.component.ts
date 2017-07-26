import {Component, Input, OnInit} from '@angular/core';
import {TodoList} from "../../../../models/models";

@Component({
  selector: 'app-todo-list',
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.sass']
})
export class TodoListComponent implements OnInit {

  @Input() todoList: TodoList = null;

  constructor() { }

  ngOnInit() {
  }

}
