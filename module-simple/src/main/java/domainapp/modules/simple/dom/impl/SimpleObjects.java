package domainapp.modules.simple.dom.impl;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDOQLTypedQuery;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.persistence.jdo.applib.services.IsisJdoSupport_v3_2;

import domainapp.modules.simple.dom.types.Name;

@DomainService(
        nature = NatureOfService.VIEW,
        objectType = "simple.SimpleObjects"
        )
public class SimpleObjects {

    private final RepositoryService repositoryService;
    private final IsisJdoSupport_v3_2 isisJdoSupport;

    @Inject
    public SimpleObjects(RepositoryService repositoryService, IsisJdoSupport_v3_2 isisJdoSupport) {
        this.repositoryService = repositoryService;
        this.isisJdoSupport = isisJdoSupport;
    }

    public static class ActionDomainEvent extends org.apache.isis.applib.events.domain.ActionDomainEvent<SimpleObjects> {}

    public static class CreateActionDomainEvent extends ActionDomainEvent {}
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = CreateActionDomainEvent.class)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_MODAL)
    public SimpleObject create(
            @Name final String name
            ) {
        return repositoryService.persist(SimpleObject.ofName(name));
    }

    public static class FindByNameActionDomainEvent extends ActionDomainEvent {}
    @Action(semantics = SemanticsOf.SAFE, domainEvent = FindByNameActionDomainEvent.class)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT, promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public List<SimpleObject> findByName(
            @Name final String name
            ) {
        JDOQLTypedQuery<SimpleObject> q = isisJdoSupport.newTypesafeQuery(SimpleObject.class);
        final QSimpleObject cand = QSimpleObject.candidate();
        q = q.filter(
                cand.name.indexOf(q.stringParameter("name")).ne(-1)
                );
        return q.setParameter("name", name)
                .executeList();
    }

    public SimpleObject findByNameExact(final String name) {
        JDOQLTypedQuery<SimpleObject> q = isisJdoSupport.newTypesafeQuery(SimpleObject.class);
        final QSimpleObject cand = QSimpleObject.candidate();
        q = q.filter(
                cand.name.eq(q.stringParameter("name"))
                );
        return q.setParameter("name", name)
                .executeUnique();
    }

    public static class ListAllActionDomainEvent extends ActionDomainEvent {}
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<SimpleObject> listAll() {
        return repositoryService.allInstances(SimpleObject.class);
    }

    public void ping() {
        JDOQLTypedQuery<SimpleObject> q = isisJdoSupport.newTypesafeQuery(SimpleObject.class);
        final QSimpleObject candidate = QSimpleObject.candidate();
        q.range(0,2);
        q.orderBy(candidate.name.asc());
        q.executeList();
    }


}
