package info.deckermail.demoemailserver.emails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
interface EmailRepository extends PagingAndSortingRepository<EmailEntity, Long>, CrudRepository<EmailEntity, Long> {

}
