package repository;

public interface Repository<E, ID> {
    E save(E entity);
    E delete(ID id);
    E findOne(ID id);
    E update(E newentity, E oldentity);
    Iterable<E> findAll();
}
