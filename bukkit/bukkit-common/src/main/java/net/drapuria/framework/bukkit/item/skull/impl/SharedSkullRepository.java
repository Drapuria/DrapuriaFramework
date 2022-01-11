package net.drapuria.framework.bukkit.item.skull.impl;

import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.repository.CrudRepository;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(name = "sharedSkullRepository")
public class SharedSkullRepository implements CrudRepository<ItemStack, String> {

    @Autowired
    private HDBRepository hdbRepository;

    @Override
    public void init() {

    }

    @Override
    public Class<?> type() {
        return ItemStack.class;
    }

    @Override
    public <S extends ItemStack> S save(S pojo) {
        throw new UnsupportedOperationException("Cannot save in shared skull repository.");
    }

    @Override
    public Optional<ItemStack> findById(String id) {
        return hdbRepository.findById(id);
    }

    @Override
    public void deleteById(String s) {
        throw new UnsupportedOperationException("Cannot delete skull in shared repository.");
    }

    @Override
    public Optional<ItemStack> findBy(String field, Object key) {
        return hdbRepository.findBy(field, key);
    }

    @Override
    public <Q> Optional<ItemStack> findByQuery(String query, Q value) {
        throw new  UnsupportedOperationException("Cannot find by query in shared skull repository.");
    }

    @Override
    public <Q> void deleteByQuery(String query, Q value) {
        throw new UnsupportedOperationException("Cannot delete by query in shared skull repository");
    }

    @Override
    public Iterable<ItemStack> findAll() {
        List<ItemStack> list = new ArrayList<>();
        hdbRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public Iterable<ItemStack> findAllById(List<String> strings) {
        return hdbRepository.findAllById(strings);
    }

    @Override
    public boolean existsById(String s) {
        if (hdbRepository.existsById(s))
            return true;
        return false;
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Cannot count in shared skull repository.");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Cannot delete all in shared skull repository.");
    }
}
