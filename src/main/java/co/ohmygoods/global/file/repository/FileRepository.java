package co.ohmygoods.global.file.repository;

import co.ohmygoods.global.file.model.entity.File;
import co.ohmygoods.global.file.model.vo.DomainType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface FileRepository extends CrudRepository<File, Long> {

    @Query("SELECT f " +
            "FROM File f " +
            "WHERE f.domainType = :domainType " +
            "AND f.domainId IN :domainIds")
    List<File> findAllByDomainTypeAndDomainIds(DomainType domainType, Collection<String> domainIds);
}
