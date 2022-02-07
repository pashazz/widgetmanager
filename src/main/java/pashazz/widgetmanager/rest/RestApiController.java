package pashazz.widgetmanager.rest;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import pashazz.widgetmanager.entity.Widget;
import pashazz.widgetmanager.entity.query.WidgetUpdateRequest;
import pashazz.widgetmanager.repository.memory.ConcurrentInMemoryWidgetRepository;
import pashazz.widgetmanager.rest.request.PaginationRequest;

import java.util.List;
import java.util.Optional;

@RestController
public class RestApiController {
  private static final int DEFAULT_PAGE_SIZE = 50;

  private final ConcurrentInMemoryWidgetRepository repo;

  public RestApiController(@NotNull ConcurrentInMemoryWidgetRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/widgets/all")
  List<Widget<Long>> all() {
    return repo.listWidgets();
  }

  @GetMapping("/widgets")
  List<Widget<Long>> page(@RequestBody Optional<PaginationRequest> request) {
    return repo.listWidgets(
      request.map(PaginationRequest::getPage).orElse(0),
      request.map(PaginationRequest::getSize).orElse(DEFAULT_PAGE_SIZE));
  }

  @GetMapping("/widgets/{id}")
  Widget<Long> one (@PathVariable Long id) {
    return repo.getWidget(id);
  }

  @PostMapping("/widgets")
  Widget<Long> createWidget(@RequestBody WidgetUpdateRequest request) {
    return repo.createWidget(request);
  }

  @PutMapping("/widgets/{id}")
  Widget<Long> updateWidget(@RequestBody WidgetUpdateRequest request, @PathVariable Long id) {return repo.updateWidget(id, request);}

  @DeleteMapping("/widgets/{id}")
  void delete(@PathVariable Long id) {
    repo.deleteWidget(id);
  }


}
