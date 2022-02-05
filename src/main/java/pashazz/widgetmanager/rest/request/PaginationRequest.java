package pashazz.widgetmanager.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;

@Data
@AllArgsConstructor
public class PaginationRequest {


  private Integer page;


  private Integer size;
}
