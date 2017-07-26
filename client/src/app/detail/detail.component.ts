import {Component, OnInit} from '@angular/core';
import {EventEmitterService} from "../../services/event-emitter.service";
import {Item, TodoList} from "../../models/models";
import {ActivatedRoute, Router} from "@angular/router";
import {TodoListsService} from "../../services/todo-lists.service";

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.sass']
})
export class DetailComponent {

  todoList: TodoList = null;
  addMode: boolean = false;
  private todoListId: number = -1;

  constructor(private router: Router, private currentRoute: ActivatedRoute, private service: TodoListsService) {
    this.currentRoute.queryParams.subscribe(params => {
      this.todoListId = params['id'];
      this.getTodoList();
    });

    EventEmitterService.get("add-item-end").subscribe(() => {
      this.addMode = false;
    });

    EventEmitterService.get("added-new-item").subscribe(() => {
      this.getTodoList();
    });
  }

  private getTodoList(): void {
    if (this.todoListId != -1) {
      this.service.getTodoListById(this.todoListId).subscribe(todoList => {
        this.todoList = todoList;
      })
    }
  }

  onTrashClick(item: Item): void {
    this.service.deleteItem(item).subscribe(() => {
      this.getTodoList();
    });
  }

  onAddNewItemClicked(): void {
    this.addMode = true;
  }

}
