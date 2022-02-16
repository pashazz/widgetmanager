package pashazz.widgetmanager.repository.db.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pashazz.widgetmanager.entity.db.JpaWidgetImpl;

import java.util.List;
import java.util.Optional;

public interface JpaWidgetRepository extends Repository<JpaWidgetImpl, Long> {


  @Query("select w from JpaWidgetImpl w order by w.z asc ")
  List<JpaWidgetImpl> findAllOrderByZAsc();


  @Query("select w from JpaWidgetImpl w order by w.z asc ")
  Page<JpaWidgetImpl> findAllOrderByZAsc(Pageable pageable);


  Optional<JpaWidgetImpl> findById(Long id);

  @Query("select max(w.z) from JpaWidgetImpl w")
  Integer getTopZ();


  Optional<JpaWidgetImpl> findByZ(int z);

  JpaWidgetImpl save(JpaWidgetImpl entity);


  void deleteById(Long id);


}
